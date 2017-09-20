package com.sishuok;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * http://10.0.0.16:8080/user/object?name=ds&id=2&
 * 手机和电脑都连同一个wifi访问
 */

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
    @Bean
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
    }

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
    /*@Bean
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
    }*/

    /**
     * Jetty redirect http to https
     * <a href= 'http://stackoverflow.com/questions/26655875/spring-boot-redirect-http-to-https'>@link</a>
     */
    /*@Bean
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
                    constraintMapping.setPathSpec("*//*");
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
    }*/


    /** jetty http2 */
    /** vm options : -Xbootclasspath/p:/Users/kingsonwu/.m2/repository/org/mortbay/jetty/alpn/alpn-boot/8.1.9.v20160720/alpn-boot-8.1.9.v20160720.jar */
    /** /usr/local/Cellar/curl/7.50.3/bin/curl --http2 -kI  https://localhost:8443/user/1 */
    /*@Bean
    public EmbeddedServletContainerCustomizer http2ServletContainerCustomizer() {
        return container -> {
            JettyEmbeddedServletContainerFactory factory = (JettyEmbeddedServletContainerFactory) container;

            factory.addServerCustomizers(new JettyServerCustomizer() {
                @Override
                public void customize(Server server) {
                    //if (serverProperties.getSsl() != null && serverProperties.getSsl().isEnabled()) {
                        ServerConnector connector = (ServerConnector) server.getConnectors()[0];
                        int port = connector.getPort();
                        SslContextFactory sslContextFactory = connector
                                .getConnectionFactory(SslConnectionFactory.class).getSslContextFactory();
                        HttpConfiguration httpConfiguration = connector
                                .getConnectionFactory(HttpConnectionFactory.class).getHttpConfiguration();

                        configureSslContextFactory(sslContextFactory);
                        ConnectionFactory[] connectionFactories = createConnectionFactories(sslContextFactory, httpConfiguration);

                        ServerConnector serverConnector = new ServerConnector(server, connectionFactories);
                        serverConnector.setPort(port);
                        // override existing connectors with new ones
                        server.setConnectors(new Connector[]{serverConnector});
                  // }
                }

                private void configureSslContextFactory(SslContextFactory sslContextFactory) {
                    sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
                    sslContextFactory.setUseCipherSuitesOrder(true);
                }

                private ConnectionFactory[] createConnectionFactories(SslContextFactory sslContextFactory,
                                                                      HttpConfiguration httpConfiguration) {
                    SslConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactory, "alpn");
                    ALPNServerConnectionFactory alpnServerConnectionFactory =
                            new ALPNServerConnectionFactory("h2", "h2-17", "h2-16", "h2-15", "h2-14");

                    HTTP2ServerConnectionFactory http2ServerConnectionFactory =
                            new HTTP2ServerConnectionFactory(httpConfiguration);

                    return new ConnectionFactory[]{sslConnectionFactory, alpnServerConnectionFactory,
                            http2ServerConnectionFactory};
                }
            });
        };
    }*/

}