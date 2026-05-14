package org.myapp;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    io.agroal.api.AgroalDataSource dataSource;

    void onStart(@Observes io.quarkus.runtime.StartupEvent ev) {
        try (java.sql.Connection conn = dataSource.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            java.sql.ResultSet rs = stmt.executeQuery("SELECT INDEX_NAME FROM information_schema.STATISTICS WHERE TABLE_SCHEMA='taskmanager' AND TABLE_NAME='board_lists' AND NON_UNIQUE=0 AND INDEX_NAME != 'PRIMARY'");
            while (rs.next()) {
                String indexName = rs.getString(1);
                try (java.sql.Statement dropStmt = conn.createStatement()) {
                    dropStmt.execute("ALTER TABLE board_lists DROP INDEX " + indexName);
                    System.out.println("Dropped legacy unique constraint: " + indexName);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not drop legacy unique constraint: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Quarkus.run(Main.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        System.out.println("========================================");
        System.out.println("  TaskFlow đã khởi động thành công!");
        System.out.println("  URL: http://localhost:8080");
        System.out.println("  Swagger: http://localhost:8080/q/swagger-ui");
        System.out.println("========================================");
        Quarkus.waitForExit();
        return 0;
    }
}
