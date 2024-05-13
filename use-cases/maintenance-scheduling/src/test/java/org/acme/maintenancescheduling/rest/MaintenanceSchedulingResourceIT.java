package org.acme.maintenancescheduling.rest;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;

import ai.timefold.solver.core.api.solver.SolverStatus;

import org.acme.maintenancescheduling.domain.MaintenanceSchedule;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

@QuarkusIntegrationTest
class MaintenanceSchedulingResourceIT {

    @Test
    void solveNative() {
        MaintenanceSchedule maintenanceSchedule = given()
                .when().get("/demo-data/SMALL")
                .then()
                .statusCode(200)
                .extract()
                .as(MaintenanceSchedule.class);

        String jobId = given()
                .contentType(ContentType.JSON)
                .body(maintenanceSchedule)
                .expect().contentType(ContentType.TEXT)
                .when().post("/schedules")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        await()
                .atMost(Duration.ofMinutes(1))
                .pollInterval(Duration.ofMillis(500L))
                .until(() -> SolverStatus.NOT_SOLVING.name().equals(
                        get("/schedules/" + jobId + "/status")
                                .jsonPath().get("solverStatus")));

        MaintenanceSchedule solution = get("/schedules/" + jobId).then().extract().as(MaintenanceSchedule.class);
        assertThat(solution).isNotNull();
    }
}