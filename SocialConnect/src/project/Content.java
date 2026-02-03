package project;

/**
 * @author 
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;

import project.enums.PostDataType;

public  class Content {
	private PostDataType typeOfData;
	private String data;
	
	
	/**
	 * Constructs a Content object with the given data.
	 * <p>
	 * The type of data is assumed to be Text. To create a Content object with a
	 * different type of data, use the {@link #Content(String, PostDataType)}
	 * constructor.
	 * 
	 * @param data
	 *            The data to be stored in the Content object.
	 */
	public Content(String data) {
		super();
		this.data = data;
		this.typeOfData = PostDataType.Text;
	}
	
	/**
	 * Constructs a Content object with the given data and type of data.
	 * 
	 * @param data
	 *            The data to be stored in the Content object.
	 * @param typeOfData
	 *            The type of data to be stored in the Content object. This
	 *            determines the type of the data parameter.
	 * 
	 * @throws FileNotFoundException
	 *             If the file specified by the data parameter could not be
	 *             found.
	 */
	public Content(String data, PostDataType typeOfData) throws FileNotFoundException {
		super();
		this.data = data;
		this.typeOfData = typeOfData;
		if (typeOfData == PostDataType.Image)
			new File(data);
		
	}

/**
 * Returns the type of data stored in this Content object.
 * 
 * @return The type of data as a PostDataType.
 */
	public PostDataType getTypeOfData() {
		return typeOfData;
	}
	
/**
 * Returns the data stored in this Content object.
 * 
 * @return The data as a String.
 */
	public String getData() {
		return data;
	}

	/**
	 * Returns a string representation of the Content object. This includes the type of data
	 * and the actual data. This is useful for debugging purposes.
	 * 
	 * @return A formatted string representing the Content object
	 */
	@Override
	public String toString() {
		return "Content [typeOfData=" + typeOfData + ", data=" + data + "]";
	}

}
