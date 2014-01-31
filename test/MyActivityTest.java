import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.wikicleta.R;
import org.wikicleta.activities.access.LandingActivity;


@RunWith(RobolectricTestRunner.class)
public class MyActivityTest {

	@Test
	public void shouldHaveHappySmiles() throws Exception {
        String hello = new LandingActivity().getResources().getString(R.string.app_name);
        assertThat(hello, equalTo("Wikicleta"));
    }
}
