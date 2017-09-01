package io.swagger.assert4j.teststeps;

import io.swagger.assert4j.teststeps.delay.DelayTestStepBuilder;
import io.swagger.assert4j.teststeps.groovyscript.GroovyScriptTestStepBuilder;
import io.swagger.assert4j.teststeps.jdbcrequest.JdbcConnection;
import io.swagger.assert4j.teststeps.mockresponse.SoapMockResponseTestStepBuilder;
import io.swagger.assert4j.teststeps.plugin.PluginTestStepBuilder;
import io.swagger.assert4j.teststeps.properties.PropertiesTestStepBuilder;
import io.swagger.assert4j.teststeps.propertytransfer.PropertyTransferBuilder;
import io.swagger.assert4j.teststeps.propertytransfer.PropertyTransferTestStepBuilder;
import io.swagger.assert4j.teststeps.restrequest.RestRequestStepBuilder;
import io.swagger.assert4j.teststeps.restrequest.RestRequestStepWithBodyBuilder;
import io.swagger.assert4j.teststeps.soaprequest.SoapRequestStepBuilder;

import java.net.URL;
import java.util.Map;

public class TestSteps {

    public enum HttpMethod {
        GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, PATCH
    }

    public static SoapRequestStepBuilder soapRequest(URL wsdlUrl) {
        return new SoapRequestStepBuilder().withWsdlAt(wsdlUrl);
    }

    public static SoapMockResponseTestStepBuilder soapMockResponse(URL wsdlUrl) {
        return new SoapMockResponseTestStepBuilder().withWsdlAt(wsdlUrl);
    }

    public static RestRequestStepBuilder<RestRequestStepBuilder> restRequest() {
        return new RestRequestStepBuilder<>(null, TestSteps.HttpMethod.GET);
    }

    public static RestRequestStepBuilder<RestRequestStepBuilder> GET(String uri) {
        return new RestRequestStepBuilder<>(uri, TestSteps.HttpMethod.GET);
    }

    public static RestRequestStepWithBodyBuilder POST(String uri) {
        return new RestRequestStepWithBodyBuilder(uri, TestSteps.HttpMethod.POST);
    }

    public static RestRequestStepWithBodyBuilder PUT(String uri) {
        return new RestRequestStepWithBodyBuilder(uri, TestSteps.HttpMethod.PUT);
    }

    public static RestRequestStepBuilder<RestRequestStepBuilder> DELETE(String uri) {
        return new RestRequestStepBuilder<>(uri, TestSteps.HttpMethod.DELETE);
    }

    public static PropertyTransferTestStepBuilder propertyTransfer() {
        return new PropertyTransferTestStepBuilder();
    }

    public static PropertyTransferTestStepBuilder propertyTransfer(PropertyTransferBuilder singleTransfer) {
        return new PropertyTransferTestStepBuilder().addTransfer(singleTransfer);
    }


    public static GroovyScriptTestStepBuilder groovyScriptStep(String scriptText) {
        return new GroovyScriptTestStepBuilder(scriptText);
    }

    public static DelayTestStepBuilder delayStep(int delayInMillis) {
        return new DelayTestStepBuilder(delayInMillis);
    }

    public static PropertiesTestStepBuilder properties() {
        return new PropertiesTestStepBuilder();
    }

    public static PropertiesTestStepBuilder properties(Map<String, String> properties) {
        return new PropertiesTestStepBuilder(properties);
    }

    public static JdbcConnection jdbcConnection(String driver, String connectionString) {
        return new JdbcConnection(driver, connectionString);
    }

    /**
     * @param pluginTestStepType test step type defined by plugin. For example one of 'MQTTPublishTestStep',
     *                           'MQTTDropConnectionTestStep' or 'MQTTReceiveTestStep' defined by MQTT ReadyAPI plugin.
     * @return PluginTestStepBuilder
     */
    public static PluginTestStepBuilder pluginTestStep(String pluginTestStepType) {
        return new PluginTestStepBuilder(pluginTestStepType);
    }
}
