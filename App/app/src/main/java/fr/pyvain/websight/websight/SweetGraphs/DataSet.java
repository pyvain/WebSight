package fr.pyvain.websight.websight.SweetGraphs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class DataSet implements Parcelable {

    private final Set<String> urls;


    public DataSet() {
        urls = new HashSet<>();
    }

    public void addURLs(Collection<String> urls) {
        for (String url : urls) {
            this.urls.add(url);
        }
    }

    public Set<String> getURLs() {
        return Collections.unmodifiableSet(urls);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataSet dataSet = (DataSet) o;
        return urls.equals(dataSet.urls);
    }

    @Override
    public int hashCode() {
        return urls.hashCode();
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringList(new ArrayList<>(urls));
    }

    public static final Parcelable.Creator<DataSet> CREATOR
            = new Parcelable.Creator<DataSet>() {
        public DataSet createFromParcel(Parcel in) {
            return new DataSet(in);
        }

        public DataSet[] newArray(int size) {
            return new DataSet[size];
        }
    };

    private DataSet(Parcel in) {
        List<String> urls = new ArrayList<>();
        in.readStringList(urls);
        this.urls = new HashSet<>(urls);
    }
}
