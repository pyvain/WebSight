package fr.pyvain.websight.websight.PersonalDataGraph;

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
 * A class that represents the data carried by an edge or a vertex of
 *  a personal data graph, i.e. a set of URLs.
 * </p>
 * <p>
 * @author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class DataSet implements Parcelable {

	/**
	 * Set of urls
	 */
	private final Set<String> urls;

	/**
	 * Initializes a new empty DataSet
	 */
	public DataSet() {
		urls = new HashSet<>();
	}

	/**
	 * Adds the urls contained in the specified set of Strings
	 * to the set of urls of the DataSet
	 *
	 * @param urls a String set containing urls
	 */
	public void addURLs(Collection<String> urls) {
		for (String url : urls) {
			this.urls.add(url);
		}
	}

	/**
	 * urls getter
	 *
	 * @return a read only set of urls contained in the DataSet
	 */
	public Set<String> getURLs() {
		return Collections.unmodifiableSet(urls);
	}

    /**
     * Two DataSet are equal if they contain the same urls
     *
     * @param o Object to compare with the receiving DataSet
     * @return True if and only if o is a DataSet which contain
	 * the same urls than the receiving DataSet.
     */
    public boolean equals(Object o) {
        if (!(o instanceof DataSet)) {
            return false;
        } else {
            DataSet that = (DataSet) o;
            return (this.getURLs().size() == that.getURLs().size() &&
                    this.getURLs().equals(that.getURLs()));
        }
    }

    /**
     * Returns a hash code value for the DataSet.
     * Two equal DataSet objects have the same hash code value.
     *
     * @return a hash code value for the DataSet.
     */
    @Override
    public int hashCode() {
        // Good implementation propose in Josh Bloch's Effective Java
        int result = 13;
        result = 37 * result + urls.hashCode();
        return result;
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
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