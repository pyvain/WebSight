import java.util.HashSet;
import java.util.Set;
import java.util.Collections;

/**
 * <p>
 * A class that represents the data carried by an edge or a vertex of
 *  a personal data graph, i.e. a set of URLs.
 * </p>
 * <p>
 * @Author Etienne Thiery, etienne.thiery@wanadoo.fr
 * </p>
 */
public final class DataSet {

	/**
	 * Set of urls
	 */
	private final HashSet<String> urls;

	/**
	 * Initializes a new empty DataSet
	 */
	public DataSet() {
		this.urls = new HashSet<String>();
	}

	/**
	 * Adds the urls contained in the specified set of Strings
	 * to the set of urls of the DataSet
	 *
	 * @param urls a String set containing urls
	 */
	public void addURLs(Set<String> urls) {
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
}