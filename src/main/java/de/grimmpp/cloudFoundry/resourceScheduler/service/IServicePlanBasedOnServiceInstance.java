package de.grimmpp.cloudFoundry.resourceScheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.grimmpp.cloudFoundry.resourceScheduler.config.AppConfig;
import de.grimmpp.cloudFoundry.resourceScheduler.helper.ObjectMapperFactory;
import de.grimmpp.cloudFoundry.resourceScheduler.helper.ServicePlanFinder;
import de.grimmpp.cloudFoundry.resourceScheduler.model.database.*;
import de.grimmpp.cloudFoundry.resourceScheduler.model.database.BindingRepository;
import de.grimmpp.cloudFoundry.resourceScheduler.model.database.ParameterRepository;
import de.grimmpp.cloudFoundry.resourceScheduler.model.database.ServiceInstance;
import de.grimmpp.cloudFoundry.resourceScheduler.model.database.ServiceInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
public abstract class IServicePlanBasedOnServiceInstance implements IServicePlan {

    @Autowired
    protected CfClient cfClient;

    @Autowired
    protected ServiceInstanceRepository siRepo;

    @Autowired
    protected BindingRepository bRepo;

    @Autowired
    protected ParameterRepository pRepo;

    @Autowired
    protected AppConfig appConfig;

    @Value("${scheduling-enabled}")
    private Boolean schedulingEnabled;

    @Value("${CF_INSTANCE_INDEX}")
    private Integer instanceIndex;

    protected ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    protected abstract void performActionForServiceInstance(ServiceInstance si) throws IOException;

    @PostConstruct
    public void init() {
        ServicePlanFinder.registerServicePlan(getServicePlanId(), this);
        if (schedulingEnabled) log.debug("Service Plan {} is activated.", getClass().getSimpleName());
        else log.debug("Service Plan {} is NOT activated.", getClass().getSimpleName());
    }

    @Override
    public void run() throws IOException {
        String planId = getServicePlanId();
        log.debug("Start run of {} and for plan id {}", getClass().getSimpleName(), planId);

        long startTime = System.currentTimeMillis();

        for(ServiceInstance si: siRepo.findByServicePlanIdAndAppInstanceIndex(planId, instanceIndex, appConfig.getAmountOfInstances())) {
            log.debug("Check service instance: {}, plan: {}, org: {}, space: {}",
                    si.getServiceInstanceId(),
                    si.getServicePlanId(),
                    si.getOrgId(),
                    si.getSpaceId());

            this.performActionForServiceInstance(si);
        }

        long d = System.currentTimeMillis() - startTime;
        long dMilli = d % 1000;
        long dSec = (d / 1000) % 60 ;
        long dMin = d / (1000 * 60);
        log.debug("Duration of run {}min {}sec {}milli", dMin, dSec, dMilli);
    }
}
