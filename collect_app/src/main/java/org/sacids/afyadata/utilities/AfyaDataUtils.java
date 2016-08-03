package org.sacids.afyadata.utilities;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Renfrid-Sacids on 5/26/2016.
 */
public class AfyaDataUtils {

    private static final String MOBILE_PATTERN = "^[0-9]{12}$";


    /**
     * Regex to validate the mobile number
     * mobile number should be of 12 digits length
     *
     * @param mobile
     * @return
     */
    public static boolean isValidPhoneNumber(String mobile) {
        return mobile.matches(MOBILE_PATTERN);
    }

    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
                int count=is.read(bytes, 0, buffer_size);
                if(count==-1)
                    break;
                os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
