# java-kanban
An application that serves as a task tracker, aiding in creating and monitoring the status of various tasks.

**Functionality:**
1. Adding, editing, and deleting tasks.
2. Viewing task history.
3. Saving application data to:
    - Application memory;
    - File;
    - Server.

**Task Properties**
Each task possesses the following properties:
1. A title providing a brief description of the task (e.g., "Moving").
2. A description where details are elaborated.
3. A unique identification number for the task, which can be used to locate it.
4. A status reflecting its progress:
    - NEW — the task has just been created, and work on it hasn't begun yet.
    - IN_PROGRESS — work is being conducted on the task.
    - DONE — the task has been completed.