package august.test;

import august.TestableApplication;
//import com.klg.jprobe.profiler.Profiler;
//import com.klg.jprobe.*;

public class ProfilerTestableImpl implements TestableApplication {
//Profiler profiler;
public ProfilerTestableImpl() {
	//profiler = new Profiler(true);
}

public void startApplication() {
    // Call main with no arguments
    //profiler.main(new String[0]);
}
public void exitApplication() {
 	// Write main frame stats to file.
 /**hongfei	if (JPApplication.appl.getFrame() != null) {
         JPApplication.appl.getFrame().writeIni();
 	}

     if (RunSettingsMgr.getInstance() != null) {
         RunSettingsMgr.getInstance().shutdown();	// save settings
     }

 	JProbe.app.disposeAllSnapshots();

     if (RunDialog.getInstance() != null) {
         RunDialog.getInstance().shutdown();
     }

 	JPApplication.appl.exit(0, false);

	hongfei**/
}
}