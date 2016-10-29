package com.sishuok;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    /**第二种方式
     通过@Configuration+@ComponentScan开启注解扫描并自动注册相应的注解Bean
     http://localhost:8080/user/1
     https://localhost:8443/user/1
     **/

    /**java -jar ...your.jar  --Dserver.port=8081 **/

    /**
     * which port the server runs on, can be configured by specifying properties either through the command line (as --D-style arguments) or through a loaded property file (Spring Boot will automatically consult any properties in a file named application.properties on the CLASSPATH, for example). Thus, to change the port on which Tomcat listens, you might specify --Dserver.port=8081, to have it listen on port 8081. If you specify server.port=0, it’ll automatically find an unused port to listen on, instead.
     **/


    /** tomcat http强制转发到https */
    /*@Bean
    public EmbeddedServletContainerFactory servletContainer() {

        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected void postProcessContext(Context context) {

                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("*//*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    private Connector initiateHttpConnector() {

        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        //一定要转发??不能直接支持??
        return connector;
    }*/

    /** tomcat https 和 https 兼容 */
    /*@Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createHttpConnector());
        return tomcat;
    }
    private Connector createHttpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        return connector;
    }*/

    /**  Jetty Https */
  /* @Bean
   public EmbeddedServletContainerFactory embeddedServletContainerFactory() throws Exception {
       return new JettyEmbeddedServletContainerFactory() {
           @Override
           protected JettyEmbeddedServletContainer getJettyEmbeddedServletContainer(
                   Server server) {

               SslContextFactory sslContextFactory = new SslContextFactory();
               sslContextFactory.setKeyStorePath("/usr/local/keystore");
               sslContextFactory.setKeyStorePassword("password");
               sslContextFactory.setCertAlias("alias");

               SslSocketConnector sslConnector = new SslSocketConnector(sslContextFactory);
               sslConnector.setPort(8443);
               server.setConnectors(new Connector[] { sslConnector });
               return super.getJettyEmbeddedServletContainer(server);
           }
       };
   }*/

    /**
     * Jetty Http
     */
   /* @Bean
    public EmbeddedServletContainerFactory embeddedServletContainerFactory() throws Exception {
        return new JettyEmbeddedServletContainerFactory() {
            @Override
            protected JettyEmbeddedServletContainer getJettyEmbeddedServletContainer(
                    Server server) {

                ServerConnector connector = new ServerConnector(server);
                connector.setPort(8080);
                server.addConnector(connector);

                return super.getJettyEmbeddedServletContainer(server);
            }
        };
    }
*/

    /**
     * Jetty redirect http to https
     * <a href= 'http://stackoverflow.com/questions/26655875/spring-boot-redirect-http-to-https'>@link</a>
     */
    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return container -> {
            JettyEmbeddedServletContainerFactory containerFactory = (JettyEmbeddedServletContainerFactory) container;
            //Add a plain HTTP connector and a WebAppContext config to force redirect from http->https
            containerFactory.addConfigurations(new AbstractConfiguration() {
                @Override
                public void configure(WebAppContext context) throws Exception {
                    Constraint constraint = new Constraint();
                    constraint.setDataConstraint(2);

                    ConstraintMapping constraintMapping = new ConstraintMapping();
                    constraintMapping.setPathSpec("/*");
                    constraintMapping.setConstraint(constraint);

                    ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
                    constraintSecurityHandler.addConstraintMapping(constraintMapping);
                    context.setSecurityHandler(constraintSecurityHandler);
                }
            });

            containerFactory.addServerCustomizers(server -> {
                HttpConfiguration http = new HttpConfiguration();
                http.setSecurePort(8443);
                http.setSecureScheme("https");

                ServerConnector connector = new ServerConnector(server);
                connector.addConnectionFactory(new HttpConnectionFactory(http));
                connector.setPort(8080);

                server.addConnector(connector);
            });
        };
    }

}