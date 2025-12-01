package com.example.bfhl;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.file.*;

@Component
public class Runner implements CommandLineRunner {

    @Value("${app.generate-url}")
    private String generateUrl;

    @Value("${app.submit-url}")
    private String submitUrl;

    @Value("${app.reg.name}")
    private String name;

    @Value("${app.reg.regNo}")
    private String regNo;

    @Value("${app.reg.email}")
    private String email;

    private final WebClient client = WebClient.create();

    @Override
    public void run(String... args) throws Exception {
        var body = java.util.Map.of(
            "name", name,
            "regNo", regNo,
            "email", email
        );

        var resp = client.post()
            .uri(generateUrl)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(java.util.Map.class)
            .block();

        System.out.println("Response: " + resp);

        String webhook = (String) resp.get("webhook");
        String token   = (String) resp.get("accessToken");
        String finalQuery = """
            WITH high_salary AS (
        SELECT 
            d.DEPARTMENT_ID,
            d.DEPARTMENT_NAME,
            e.EMP_ID,
            e.FIRST_NAME,
            e.LAST_NAME,
            e.DOB,
            p.AMOUNT
        FROM EMPLOYEE e
        JOIN DEPARTMENT d 
            ON e.DEPARTMENT = d.DEPARTMENT_ID
        JOIN PAYMENTS p 
            ON e.EMP_ID = p.EMP_ID
        WHERE p.AMOUNT > 70000
    ),
    with_age AS (
        SELECT
            DEPARTMENT_ID,
            DEPARTMENT_NAME,
            EMP_ID,
            FIRST_NAME,
            LAST_NAME,
            EXTRACT(YEAR FROM AGE(DATE(p.PAYMENT_TIME), DOB)) AS AGE
        FROM high_salary h
        JOIN PAYMENTS p 
            ON h.EMP_ID = p.EMP_ID
        WHERE p.AMOUNT > 70000
    )
    SELECT
        DEPARTMENT_NAME,
        ROUND(AVG(AGE), 2) AS AVERAGE_AGE,
        STRING_AGG(FIRST_NAME || ' ' || LAST_NAME, ', ' ORDER BY FIRST_NAME, LAST_NAME)
            WITHIN GROUP (ORDER BY FIRST_NAME)
            LIMIT 10 AS EMPLOYEE_LIST
    FROM with_age
    GROUP BY DEPARTMENT_NAME
    ORDER BY MAX(DEPARTMENT_ID) DESC;
        """;
        Files.writeString(Path.of("finalQuery.sql"), finalQuery);
        var submitBody = java.util.Map.of("finalQuery", finalQuery);
        var submitResp = client.post()
            .uri(webhook)
            .header("Authorization", token)
            .bodyValue(submitBody)
            .retrieve()
            .bodyToMono(java.util.Map.class)
            .block();
        System.out.println("Submit Response: " + submitResp);
    }
}
