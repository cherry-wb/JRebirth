import org.jrebirth.preloader.JRebirthPreloader;
import org.jrebirth.sample.SampleApplication;

import org.junit.Test;

import com.sun.javafx.application.LauncherImpl;

public class WithPreloader {

    @Test
    public void test() {
        LauncherImpl.launchApplication(SampleApplication.class, JRebirthPreloader.class, null);
    }

}