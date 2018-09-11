package com.bravo.BlueTooth;

import com.bravo.utils.Logs;

public class Mutex {
        private boolean syncLock;
        private String TAG = "Mutex";
        ////////////////////////////////////////////////
        //  Constructor
        ////////////////////////////////////////////////

        public Mutex()
        {
            syncLock = false;
        }

        ////////////////////////////////////////////////
        //  lock
        ////////////////////////////////////////////////

        public synchronized void lock()
        {
            while(syncLock == true) {
                try {
                    wait();
                }
                catch (Exception e) {
                    Logs.e(TAG, "Mutex exception=" + e.getMessage());
                };
            }
            syncLock = true;
        }

        public synchronized void unlock()
        {
            syncLock = false;
            notifyAll();
        }
}
