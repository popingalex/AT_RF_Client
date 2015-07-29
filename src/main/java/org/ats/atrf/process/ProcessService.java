package org.ats.atrf.process;

import org.ats.atrf.service.AbstractService;

/**
 * 
 * @author Alex Xu
 */
public class ProcessService extends AbstractService {
    private static String[] DEFAULT_COMMANDS = new String[] { "java" };
    private static String[] DEFAULT_SOURCES  = new String[0];
    private String[]        commands         = DEFAULT_COMMANDS;
    private Object[]        sources          = DEFAULT_SOURCES;
    @Override
    public void service(ServiceContent content) {
        beforeProcess(content);

        Processor processor = new Processor(commands);
        processor.writeSource(sources);

        System.out.println(processor.process());
        afterProcess(content);
    }
    /**
     * <div class="en">
     * 
     * Do something before processing<br>
     * </div>
     * 
     * <div class="cn">
     * 
     * 在执行进程前干点儿啥<br>
     * </div>
     * 
     * @param content
     */
    protected void beforeProcess(ServiceContent content) {
        setCommands((String[]) content.get("commands"));
        setSources((Object[]) content.get("sources"));
    }

    /**
     * <div class="en">
     * 
     * Do something after processing<br>
     * </div>
     * 
     * <div class="cn">
     * 
     * 在执行进程后干点儿啥<br>
     * </div>
     * 
     * @param content
     */
    protected void afterProcess(ServiceContent content) {
    }

    protected void setCommands(String... commands) {
        this.commands = (commands == null || commands.length == 0) ? DEFAULT_COMMANDS : commands;
    }

    protected void setSources(Object... sources) {
        this.sources = (sources == null) ? DEFAULT_SOURCES : sources;
    }
    protected void handleEvent() {
    }
}
