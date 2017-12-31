[![Build Status](https://api.travis-ci.org/symbiote-h2020/RegistrationHandlerClient.svg?branch=develop)](https://api.travis-ci.org/symbiote-h2020/RegistrationHandlerClient)
[![codecov.io](https://codecov.io/github/symbiote-h2020/RegistrationHandlerClient/branch/staging/graph/badge.svg)](https://codecov.io/github/symbiote-h2020/RegistrationHandlerClient)
[![](https://jitpack.io/v/symbiote-h2020/RegistrationHandlerClient.svg)](https://jitpack.io/#symbiote-h2020/RegistrationHandlerClient)

# Registration Handler Client

## Using Registration Handler Client

The idea of Generic EnablerLogic is to use it as dependency in any Spring porject.
It uses Eureka to find RegistrationHandler component. 

### 1. Creating new SpringBoot project

Create standard SpringBoot project.

### 2. Adding symbIoTe dependencies to `build.gradle`

Add following dependencies for cutting edge version:

`compile('com.github.symbiote-h2020:RegistrationHandlerClient:develop-SNAPSHOT') { changing = true }`

or add following for specific version:

`compile('com.github.symbiote-h2020:RegistrationHandlerClient:develop:{version}')`

Current version is `1.1.0`.

This is dependency from jitpack repository. 
In order to use jitpack you need to put in `build.gradle` 
following lines as well:

```
allprojects {
	repositories {
		jcenter()
		maven { url "https://jitpack.io" }
	}
}
```

### 3. Setting configuration

Configuration needs to be put in `bootstrap.properties` or YMl file. An example is here:

```
spring.application.name=RHClientExample
```
The first line is defining the name of this SpringBoot application that will
be registered in Eureka.

### 4. Registering resources

For communication with Registration Handler component there is service
`RegistrationHandlerClientService` which can be injected in any spring
component like this:

```java
@Autowired
private RegistrationHandlerClientService rhClientService;
```

There are different methods for registering, unregistering and updating
resources. Each of this methods return list of `CloudResource` objects 
which represents registered resources.

In the `CloudResoure` class should be put RAP plugin id which will be 
responsible for serving data. If you are using this dependency with
RAP plugin the plugin id can be obtained from  `RapPluginProperties` object that can be
injected. The method `getPluginName()` returns plugin id.

Here is example of registration:
```java
@Component
public class SomeComponent {
...
    
    @Autowired
    private RapPluginProperties props;
    
    @Autowired
    private RegistrationHandlerClientService rhClientService;

    private void registerResources() {
        List<CloudResource> cloudResources = new LinkedList<>();
        cloudResources.add(createSensorResource("1000"));
        cloudResources.add(createActuatorResource("2000"));
        cloudResources.add(createServiceResource("3000"));

        // waiting for registrationHandler to create exchange
        int i = 1;
        while(i < 10) {
            try {
                LOG.debug("Atempting to register resources count {}.", i);
                rhClientService.registerResources(cloudResources);
                LOG.debug("Resources registered");
                break;
            } catch (Exception e) {
                i++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                }
            }
        }
    }
    
    private CloudResource createSensorResource(String internalId) {
        CloudResource cloudResource = new CloudResource();
        cloudResource.setInternalId(internalId);
        cloudResource.setPluginId(props.getPluginName());
        cloudResource.setCloudMonitoringHost("cloudMonitoringHostIP");

        StationarySensor sensor = new StationarySensor();
        cloudResource.setResource(sensor);
        sensor.setLabels(Arrays.asList("termometer"));
        sensor.setComments(Arrays.asList("A comment"));
        sensor.setInterworkingServiceURL("https://symbiote-h2020.eu/example/interworkingService/");
        sensor.setLocatedAt(new WGS84Location(2.349014, 48.864716, 15, 
                Arrays.asList("Paris"), 
                Arrays.asList("This is Paris")));
        FeatureOfInterest featureOfInterest = new FeatureOfInterest();
        sensor.setFeatureOfInterest(featureOfInterest);
        featureOfInterest.setLabels(Arrays.asList("Room1"));
        featureOfInterest.setComments(Arrays.asList("This is room 1"));
        featureOfInterest.setHasProperty(Arrays.asList("temperature"));
        sensor.setObservesProperty(Arrays.asList("temperature,humidity".split(",")));
        
        CloudResourceParams cloudResourceParams = new CloudResourceParams();
        cloudResource.setParams(cloudResourceParams);
        cloudResourceParams.setType("Type of device, used in monitoring");

        return cloudResource;
    }

    private CloudResource createActuatorResource(String internalId) {
        CloudResource cloudResource = new CloudResource();
        cloudResource.setInternalId(internalId);
        cloudResource.setPluginId(props.getPluginName());
        cloudResource.setCloudMonitoringHost("cloudMonitoringHostIP");
        
        Actuator actuator = new Actuator();
        cloudResource.setResource(actuator);
        actuator.setLabels(Arrays.asList("lamp"));
        actuator.setComments(Arrays.asList("A comment"));
        actuator.setInterworkingServiceURL("https://symbiote-h2020.eu/example/interworkingService/");
        actuator.setLocatedAt(new WGS84Location(2.349014, 48.864716, 15, 
                Arrays.asList("Paris"), 
                Arrays.asList("This is Paris")));
        
        Capability capability = new Capability();
        actuator.setCapabilities(Arrays.asList(capability));
        Effect effect = new Effect();
        capability.setEffects(Arrays.asList(effect));
        FeatureOfInterest featureOfInterest = new FeatureOfInterest();
        effect.setActsOn(featureOfInterest);
        Parameter parameter = new Parameter();
        capability.setParameters(Arrays.asList(parameter));
        parameter.setMandatory(true);
        parameter.setName("light");
        EnumRestriction enumRestriction = new EnumRestriction();
        enumRestriction.setValues(Arrays.asList("on", "off"));
        parameter.setRestrictions(Arrays.asList(enumRestriction));

        return cloudResource;
    }
    
    private CloudResource createServiceResource(String internalId) {
        CloudResource cloudResource = new CloudResource();
        cloudResource.setInternalId(internalId);
        cloudResource.setPluginId(props.getPluginName());
        cloudResource.setCloudMonitoringHost("cloudMonitoringHostIP");
        
        Service service = new Service();
        cloudResource.setResource(service);
        service.setLabels(Arrays.asList("lamp"));
        service.setComments(Arrays.asList("A comment"));
        service.setInterworkingServiceURL("https://symbiote-h2020.eu/example/interworkingService/");
        
        service.setName("Heat alarm");
        Parameter parameter = new Parameter();
        parameter.setMandatory(true);
        parameter.setName("trasholdTemperature");
        service.setParameters(Arrays.asList(parameter));

        return cloudResource;
    }
```

## Running

You can run this enabler as any other spring boot application.

`./gradlew bootRun`

or

`java -jar build/libs/RHClientExample-0.0.1-SNAPSHOT.jar`

Note: In order to function correctly you need to start Eureka and RegistrationHandler component before.

