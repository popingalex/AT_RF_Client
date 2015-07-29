package org.ats.atrf.service.task;

import org.ats.atrf.service.AbstractService;

public class TaskLoopService extends AbstractService {

    @Override
    public void service(ServiceContent contnet) {
        
    }
//    @Override
//    public void perform(Object... arguments) {
//        StringBuffer debugString = new StringBuffer();
//
//        // Collections.max(coll)
//        // get the earliest task
//        List<AbstractTask> taskList = TaskScheduler.getJobs(null);
//        debugString.append("[Task Loop] Tasks ").append(taskList.size());
//        debugString.append(System.getProperty("line.separator"));
//        debugString.append("[Task Loop] Current Time:" + arguments[0]);
//        debugString.append(System.getProperty("line.separator"));
//        if (taskList.isEmpty()) {
//            debugString.append("[Task Loop] Task List is Empty");
//            debugString.append(System.getProperty("line.separator"));
//        } else {
//            Collections.sort(taskList, new Comparator<AbstractTask>() {
//                @Override
//                public int compare(AbstractTask t1, AbstractTask t2) {
//                    Long time1 = t1.latest.getTime() + t1.donetimes * t1.interval * 1000L;
//                    Long time2 = t2.latest.getTime() + t2.donetimes * t2.interval * 1000L;
//                    return (int) (time1 - time2);
//                }
//            });
//            for (AbstractTask task : taskList) {
//                if (task.schedule.compareTo(new Date()) > 0) {
//                    // not now.
//                } else if (task.latest.getTime() + (task.donetimes > 0 ? 1 : 0) * task.interval * 1000L < (Long) arguments[0]) {
//                    TaskScheduler.dequeue(task);
//
//                    task.latest = new Date((Long) arguments[0]);
//                    task.donetimes++;
//
//                    debugString.append("[Task Loop] Task name :").append(task);
//                    debugString.append(System.getProperty("line.separator"));
//                    debugString.append("[Task Loop] Task count : ").append(task.donetimes).append("/")
//                            .append(task.dotimes);
//                    debugString.append(System.getProperty("line.separator"));
//
//                    if (task.donetimes < task.dotimes) {
//                        // process
//                        TaskScheduler.addJob(task);
//                    }
//                    break;
//                }
//            }
//        }
//        if (!taskList.isEmpty()) {
//            System.out.print(debugString);
//        }
//    }
}
