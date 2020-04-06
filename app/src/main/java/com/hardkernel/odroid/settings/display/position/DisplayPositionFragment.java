/* Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.hardkernel.odroid.settings.display.position;

import android.content.Context;
import android.os.Bundle;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.TwoStatePreference;
import android.support.v7.preference.PreferenceCategory;

import com.droidlogic.app.DisplayPositionManager;
import com.hardkernel.odroid.settings.R;
import com.hardkernel.odroid.settings.ConfigEnv;
import com.hardkernel.odroid.settings.LeanbackAddBackPreferenceFragment;

public class DisplayPositionFragment extends LeanbackAddBackPreferenceFragment {
    private static final String TAG = "DisplayPositionFragment";

    private static final String SCREEN_POSITION_SCALE = "screen_position_scale";
    private static final String ZOOM_IN = "zoom_in";
    private static final String ZOOM_OUT = "zoom_out";

    private DisplayPositionManager mDisplayPositionManager;

    private Preference screenPref;
    private PreferenceCategory mPref;
    private Preference zoominPref;
    private Preference zoomoutPref;

    public static DisplayPositionFragment newInstance() {
        return new DisplayPositionFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.display_position, null);
        mDisplayPositionManager = new DisplayPositionManager((Context)getActivity());

        mPref       = (PreferenceCategory) findPreference(SCREEN_POSITION_SCALE);
        zoominPref  = (Preference) findPreference(ZOOM_IN);
        zoomoutPref = (Preference) findPreference(ZOOM_OUT);

        updateMainScreen();
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case ZOOM_IN:
                mDisplayPositionManager.zoomIn();
                break;
            case ZOOM_OUT:
                mDisplayPositionManager.zoomOut();
                break;
        }
        updateMainScreen();
        return true;
    }


    private void updateMainScreen() {
        int percent = mDisplayPositionManager.getCurrentRateValue();
        mPref.setTitle("current scaling is " + percent +"%");
        ConfigEnv.setDisplayZoom(percent);
    }
}
