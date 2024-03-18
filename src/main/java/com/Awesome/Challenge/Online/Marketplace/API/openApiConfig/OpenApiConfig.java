package com.Awesome.Challenge.Online.Marketplace.API.openApiConfig;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Maurice Shema",
                        email = "munyinya13@gmail.com",
                        url = "http://127.0.0.1:23991/api/l1/listed"
                ),
                description = "OpenApi documentation for Online-Marketplace-API Spring boot application",
                title = "Online-Marketplace-API",
                version = "1.0",
                license = @License(
                        name = "You can log-in as ADMIN by using this : 'Email: admin@user.com == Password: Admin123@'" +
                                " after that you have access to authorize any other user through '\"/api/l2/users/updateRole/{userIdToUpdate}\",'  ",

                        url = "http://127.0.0.1:23991/api/l2/users/updateRole/{userIdToUpdate}"
                )

        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:23991"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)


public class OpenApiConfig {
}
