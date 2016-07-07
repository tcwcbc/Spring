package junit.template;

import java.io.BufferedReader;
import java.io.IOException;

public interface BufferedReaderCallBack {
	Integer doSomethingWithReader(BufferedReader reader) throws IOException;
}
