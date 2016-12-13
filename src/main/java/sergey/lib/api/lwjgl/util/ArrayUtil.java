package sergey.lib.api.lwjgl.util;

import java.util.Collection;

/**
 * 
 * This class contains static methods that help with array manipulation
 * 
 * @author sergeys
 *
 */
public class ArrayUtil {
	/**
	 * Combines a collection of arrays into a single long array
	 * This method is slow, requiring two total passes through the array
	 * 
	 * @param collection the collection to string together
	 * @return an array containing all of the elements of the arrays in the collection
	 */
	public static <T> T[] stringCollectionData(Collection<T[]> collection) {
		int len = 0;
		for (T[] arr : collection) {
			len += arr.length;
		}
		return stringCollectionData(collection, len);
	}
		
	/**
	 * Combines a collection of arrays into a single long array
	 * This method is as fast as possible, but it needs to know the final length of the array
	 * 
	 * @param collection the collection to string together
	 * @param totalLength the final length
	 * @return an array containing all of the elements of the arrays in the collection
	 */
	public static <T> T[] stringCollectionData(Collection<T[]> collection, int totalLength) {
		@SuppressWarnings("unchecked")
		T[] result = (T[]) new Object[totalLength];
		
		int pos = 0;
		for (T[] arr : collection) {
			System.arraycopy(arr, 0, result, pos, arr.length);
			pos += arr.length;
		}
		
		return result;
	}
}
