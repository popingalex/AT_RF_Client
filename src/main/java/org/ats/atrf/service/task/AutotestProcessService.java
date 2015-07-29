package org.ats.atrf.service.task;

import org.ats.atrf.process.ProcessService;

public class AutotestProcessService extends ProcessService {
    // TODO if quartz have something like this, use that instead.
    private static Object processlock = "shylock";

    @Override
    public void service(ServiceContent content) {
        synchronized (processlock) {
            super.service(content);
        }
    }
}
