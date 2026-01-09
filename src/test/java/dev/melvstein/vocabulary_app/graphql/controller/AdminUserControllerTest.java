package dev.melvstein.vocabulary_app.graphql.controller;

import dev.melvstein.vocabulary_app.model.AdminUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;

@GraphQlTest(AdminUserController.class)
public class AdminUserControllerTest {
    @Autowired
    private GraphQlTester graphQlTester;

    @Test
    public void testGetAllAdminUsers() {
        graphQlTester
                .documentName("getAllAdminUsers")
                .execute()
                .path("getAllAdminUsers")
                .entityList(AdminUser.class)
                .hasSize(0); // Adjust size based on test data
    }
}
