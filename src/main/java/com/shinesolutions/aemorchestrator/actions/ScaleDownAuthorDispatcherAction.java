package com.shinesolutions.aemorchestrator.actions;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.shinesolutions.aemorchestrator.aem.AgentRunMode;
import com.shinesolutions.aemorchestrator.aem.FlushAgentManager;
import com.shinesolutions.aemorchestrator.service.AemLookupService;

@Component
public class ScaleDownAuthorDispatcherAction implements ScaleAction {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Resource
    private FlushAgentManager flushAgentManager;
    
    @Resource
    private AemLookupService aemAwsLookupService;
    
    public boolean execute(String instanceId) {
        logger.info("ScaleDownAuthorDispatcherAction executing");

        return flushAgentManager.deleteFlushAgent(instanceId, AgentRunMode.AUTHOR);
    }

}