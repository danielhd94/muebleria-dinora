package extras;


public class LongTask {
    private int lengthOfTask;
    private int current = 0;
    private String statMessage;

    public LongTask (int longtask) {
	//compute length of task ...
	//in a real program, this would figure out
	//the number of bytes to read or whatever
	lengthOfTask = longtask;
    }

    //called from ProgressBarDemo to start the task
    public void go() {
	current = 0;
        final SwingWorker worker = new SwingWorker() {
            public Object construct() {
                return new ActualTask();
            }
        };
    }

    //called from ProgressBarDemo to find out how much work needs to be done
    public int getLengthOfTask() {
	return lengthOfTask;
    }

    //called from ProgressBarDemo to find out how much has been done
    public int getCurrent() {
	return current;
    }

    public void stop() {
	current = lengthOfTask;
    }

    //called from ProgressBarDemo to find out if the task has completed
    public boolean done() {
	if (current >= lengthOfTask)
	    return true;
	else
	    return false;
    }

    public String getMessage() {
	return statMessage;
    }

    //the actual long running task, this runs in a SwingWorker thread
    public class ActualTask {
	ActualTask () {
	    //fake a long task,
	    //make a random amount of progress every second
	    while (current < lengthOfTask) {
                try {
                    Thread.sleep(1000); //sleep for a second
		    current += Math.random() * 100; //make some progress
                } catch (InterruptedException e) {
                }
	    }
	}
    }
}
