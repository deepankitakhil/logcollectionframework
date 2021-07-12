# Log Collection Framework:
    #Assumption:
      1: System has sufficient read access.
      2: Logs that need to be queried is pre-registered with the system.
      3: No security check is needed.
      4: Most update write is available for read after 30 seconds (configurable).
      5: Except FNF exception, other exceptions will be swallowed silently.
    # System design:
      1:  A file watcher is created which runs every 30 seconds (configurable amount of time in seconds).
      2: For each supported log file, a file watcher is registered to keep track of file changes (currently set to Edit).
      3: Create a custom implementation of input stream using RandomAccessFile and reset the seek to the last of the file if an event is triggered by file watcher.
      4: If the file content has changed, reset the seek to the end of the file.  Please note that the solution is designed to keep Availability and Partition tolerance in mind. 
      5: If a log file content is changed, it won't block the thread to read the latest snapshot. Snapshot will be updated every 30 seconds.
    # Extension:
      1: Add unit tests.
      2: Add a cache with TTL of 30 seconds and the key would be file_path. If cache hit evaluates to true and number of lines can be satisfied by cache lookup, result will be served from the cache.
      3: Expose the functionality as a web service.
      
    # Demo:
      1: Download the code and update the root path in 'LogCollectionDemo'. Currently its set to `/Users/Deepankit/Downloads`
      2: Create a folder /var/logs and create 2 files 1.txt and 2.txt.
      3: Run the program. `1.txt` and `2.txt` will be periodically updated by two independent threads. Meanwhile, another thread will trigger read requests.
