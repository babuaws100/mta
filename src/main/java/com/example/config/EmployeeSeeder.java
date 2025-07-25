package com.example.config;

import com.example.entity.Employee;
import com.example.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.nio.file.Files;
import java.util.List;

@Component
//@DependsOn("schemaInitializer")
public class EmployeeSeeder {

    @Value("${app.seed.enabled:false}")
    private boolean seedEnabled;

    @Autowired
    @Qualifier("appDataSource")
    private DataSource appDataSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @PostConstruct
    public void seed() throws Exception {
        if (!seedEnabled) {
            System.out.println("Seeding disabled");
            return;
        }

        insertForTenant("us", "classpath:us_emp.sql");
        insertForTenant("uk", "classpath:uk_emp.sql");
    }

    private void insertForTenant(String tenantId, String sqlPath) throws Exception {
        TenantContext.setTenantId(tenantId);
        try {
            Resource resource = resourceLoader.getResource(sqlPath);
            List<String> lines = Files.readAllLines(resource.getFile().toPath());
            for (String sql : lines) {
                if (!sql.trim().isEmpty()) {
                    new JdbcTemplate(appDataSource).execute(sql);
                }
            }
            System.out.println("Seeded employees for tenant: " + tenantId);
        } finally {
            TenantContext.clear();
        }
    }
}
