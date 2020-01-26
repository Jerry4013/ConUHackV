package conuhackv.back.controller;

import conuhackv.back.CommonReturnType;
import conuhackv.back.dataobject.Image;
import conuhackv.back.model.ImageModel;
import conuhackv.back.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * @author: Jingchao Zhang
 * @createDate: 2019/10/20
 **/
@RestController
@RequestMapping("/image")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping(value = "/upload")
    public CommonReturnType upload(@RequestBody ImageModel imageModel) throws IOException, MessagingException {
        if (imageModel == null) {
            return null;
        }
        ImageModel newImage = imageService.uploadImage(imageModel);
        return CommonReturnType.create(newImage);
    }



    public ImageService getImageService() {
        return imageService;
    }
}
