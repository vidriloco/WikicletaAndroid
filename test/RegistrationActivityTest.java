import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.TestHttpResponse;
import org.robolectric.tester.org.apache.http.impl.client.DefaultRequestDirector;
import org.robolectric.util.Strings;
import org.wikicleta.R;
import org.wikicleta.activities.access.RegistrationActivity;

import android.widget.EditText;
import android.widget.ImageView;

@RunWith(RobolectricTestRunner.class)
public class RegistrationActivityTest {

	private int aceptButtonId;
	private RegistrationActivity activity;
	private DefaultRequestDirector requestDirector;
	private ConnectionKeepAliveStrategy connectionKeepAliveStrategy;

	// @Before
	// public void setup() {
	// activity = Robolectric.buildActivity(RegistrationActivity.class)
	// .create().get();
	// }

	@Before
	public void setUp_EnsureStaticStateIsReset() {
		FakeHttpLayer fakeHttpLayer = Robolectric.getFakeHttpLayer();
		assertFalse(fakeHttpLayer.hasPendingResponses());
		assertFalse(fakeHttpLayer.hasRequestInfos());
		assertFalse(fakeHttpLayer.hasResponseRules());
		assertNull(fakeHttpLayer.getDefaultResponse());

		connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse httpResponse,
					HttpContext httpContext) {
				return 0;
			}
		};
		requestDirector = new DefaultRequestDirector(null, null, null,
				connectionKeepAliveStrategy, null, null, null, null, null,
				null, null, null);
	}

	@After
	public void tearDown_EnsureStaticStateIsReset() throws Exception {
		Robolectric.addPendingHttpResponse(200, "a happy response body");
	}

	@Test
	public void RegistrationTest() throws Exception {
		aceptButtonId = R.id.save_profile;
		ImageView ivRegister = (ImageView) activity.findViewById(aceptButtonId);
		EditText mNameView = (EditText) activity.findViewById(R.id.name);
		EditText mEmailView = (EditText) activity.findViewById(R.id.email);
		EditText mUsernameView = (EditText) activity
				.findViewById(R.id.username);
		EditText mPasswordView = (EditText) activity
				.findViewById(R.id.password);
		EditText mPasswordConfirmationView = (EditText) activity
				.findViewById(R.id.password_confirmation);
		mNameView.setText("Miriam");
		mEmailView.setText("miri@gmail.com");
		mUsernameView.setText("miri");
		mPasswordView.setText("miriprueba");
		mPasswordConfirmationView.setText("miriprueba");
		ivRegister.performClick();
		Robolectric.addPendingHttpResponse(200, "a happy response body");
		Robolectric.addHttpResponseRule("http://50.56.30.227:3000//api/users",
				new TestHttpResponse(200, "a cheery response body"));

		HttpResponse postResponse = requestDirector.execute(null, new HttpPost(
				"http://50.56.30.227:3000//api/users"), null);
		assertNotNull(postResponse);
		assertThat(postResponse.getStatusLine().getStatusCode(), equalTo(200));
		assertThat(Strings.fromStream(postResponse.getEntity().getContent()),
				equalTo("a cheery response body"));

		// Robolectric.addPendingHttpResponse(200, "ok");
		// RequestMatcherBuilder myRequest = new
		// FakeHttpLayer.RequestMatcherBuilder().host("http://50.56.30.227:3000//api/users")
		// .method("POST")
		// .param("username", "Miriam")
		// .param("email", "miri@gmail.com")
		// .param("password", "miri")
		// .param("password_confirmation", "miriprueba");
		// Robolectric.addHttpResponseRule(requestMatcher, response)

		assertThat(ShadowToast.getTextOfLatestToast(),
				equalTo("debe ser alfanumérico y tener más de 5 letras"));
	}


	@Test
	public void shouldGetHttpResponseFromExecuteSimpleApi() throws Exception {
		Robolectric.addPendingHttpResponse(200, "a happy response body");
		HttpResponse response = requestDirector.execute(null, new HttpGet(
				"http://google.com"), null);

		assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
		assertThat(Strings.fromStream(response.getEntity().getContent()),
				equalTo("a happy response body"));
	}

}
