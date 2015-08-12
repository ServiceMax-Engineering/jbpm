package org.jbpm.osgi.flow.compiler;

import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.compiler.ProcessBuilderFactoryService;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.kie.api.Service;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

public class Activator
    implements
    BundleActivator {

    public void start(BundleContext bc) throws Exception {
        //BPM-1302 The timer start rule should use the Intalio method to start a process.
        //this.processBuilderReg = bc.registerService( new String[]{ ProcessBuilderFactoryService.class.getName(), Service.class.getName()},
        //                                                           new ProcessBuilderFactoryServiceImpl(),
        //                                                           new Hashtable() );
        //ProcessBuilderFactory.resetInitialization();
    }

    public void stop(BundleContext bc) throws Exception {
        //BPM-1302 The timer start rule should use the Intalio method to start a process.
       //this.processBuilderReg.unregister();
    }

}
