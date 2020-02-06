package conuhackv.back.service;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.vision.v1.*;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import conuhackv.back.dataobject.Image;
import conuhackv.back.model.ImageModel;
import conuhackv.back.repository.ImageJpaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageJpaRepository imageJpaRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${google.jsonPath}")
    private String jsonPath;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${googleURL}")
    private String googleURL;

    @Value("${google.auth}")
    private String elements;

    @Value("${email.title}")
    private String emailTitle;

    @Value("${email.content}")
    private String emailContent;

    @Value("${email.to}")
    private String to;

    @Override
    @Transactional
    public ImageModel uploadImage(ImageModel imageModel) throws Exception {
        if (imageModel == null) {
            return null;
        }
        if (!isIncident(imageModel.getImage()) && !detectWebDetections(imageModel.getImage())) {
            return null;
        }
        Image image = new Image();
        BeanUtils.copyProperties(imageModel, image);
        Image save = imageJpaRepository.save(image);
        ImageModel newImageModel = new ImageModel();
        BeanUtils.copyProperties(save, newImageModel);
        byte[] clone = save.getImage().clone();
        newImageModel.setImage(clone);

        try (FileOutputStream fos = new FileOutputStream("C:\\code\\ConUHack\\src\\main\\resources\\static\\test.jpg")) {
            fos.write(clone);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resourcePath = "C:\\code\\ConUHack\\src\\main\\resources\\static\\test.jpg";
        String id = "img";
        String content = htmlContent(id);
        try {
            sendImageEmail(to, emailTitle, content, resourcePath, id);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return newImageModel;
    }

    private boolean isIncident (byte[] bytes) throws IOException {
        authExplicit(jsonPath);
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            ByteString imgBytes = ByteString.copyFrom(bytes);

            List<AnnotateImageRequest> requests = new ArrayList<>();
            com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return false;
                }
                int count = 0;
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    Map<Descriptors.FieldDescriptor, Object> fields = annotation.getAllFields();
                    for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : fields.entrySet()) {
                        if (entry.getKey().toString().equals("google.cloud.vision.v1.EntityAnnotation.description")) {
                            String value = entry.getValue().toString();
                            System.out.printf("%s : %s\n", entry.getKey(), value);
                            if (value.equals("Flood") || value.equals("Flooding") ) {
                                return true;
                            }
                            if (value.equals("Flooring") || value.equals("Water")) {
                                count++;
                            }
                            if (count >= 2) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean detectWebDetections(byte[] bytes) throws Exception {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        ByteString imgBytes = ByteString.copyFrom(bytes);

        com.google.cloud.vision.v1.Image img = com.google.cloud.vision.v1.Image.newBuilder().
                setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.WEB_DETECTION).build();
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
        requests.add(request);

        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return false;
                }

                WebDetection annotation = res.getWebDetection();
                for (WebDetection.WebEntity entity : annotation.getWebEntitiesList()) {
                    String description = entity.getDescription();
                    if (description.equals("Flood") || description.equals("Flooding")
                            || description.equals("Water damage")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static void authExplicit(String jsonPath) throws IOException {
        // You can specify a credential file by providing a path to GoogleCredentials.
        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(jsonPath))
                .createScoped(Lists.newArrayList("elements"));
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        System.out.println("Buckets:");
        Page<Bucket> buckets = storage.list();
        for (Bucket bucket : buckets.iterateAll()) {
            System.out.println(bucket.toString());
        }
    }

    private String htmlContent(String id) {
        return "<html><body><h2>" + emailContent + "</h2><img src=\'cid:" + id + "\'></img></body></html>";
    }

    private void sendImageEmail(String to, String subject, String content, String resourcePath, String resourceId)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        FileSystemResource fileSystemResource = new FileSystemResource(new File(resourcePath));
        helper.addInline(resourceId, fileSystemResource);
        mailSender.send(message);
    }

}
