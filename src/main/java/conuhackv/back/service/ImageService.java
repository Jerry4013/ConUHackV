package conuhackv.back.service;

import conuhackv.back.model.ImageModel;

import javax.mail.MessagingException;
import java.io.IOException;


/**
 * @author: Jingchao Zhang
 * @createdate: 2019/11/05
 **/

public interface ImageService {

    ImageModel uploadImage(ImageModel imageModel);

}
