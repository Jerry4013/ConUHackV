package conuhackv.back.service;


import conuhackv.back.dataobject.Image;
import conuhackv.back.model.ImageModel;
import conuhackv.back.repository.ImageJpaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageJpaRepository imageJpaRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${googleURL}")
    private String googleURL;

    @Value("${email.title}")
    private String emailTitle;

    @Value("${email.content}")
    private String emailContent;

    @Value("${email.to}")
    private String to;

    @Override
    @Transactional
    public ImageModel uploadImage(ImageModel imageModel) {
        if (imageModel == null) {
            return null;
        }
        if (!isIncident(imageModel.getImage())) {
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

    private boolean isIncident (byte[] bytes) {
//        URL url = new URL(googleURL);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//
//
//        AnnotateImageResponse response = this.cloudVisionTemplate.analyzeImage(
//                this.resourceLoader.getResource(imageUrl), Feature.Type.LABEL_DETECTION);
//
//        Map<String, Float> imageLabels =
//                response.getLabelAnnotationsList()
//                        .stream()
//                        .collect(
//                                Collectors.toMap(
//                                        EntityAnnotation::getDescription,
//                                        EntityAnnotation::getScore,
//                                        (u, v) -> {
//                                            throw new IllegalStateException(String.format("Duplicate key %s", u));
//                                        },
//                                        LinkedHashMap::new));

        return true;
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

//    public static void detectLabels(String filePath, PrintStream out) throws Exception, IOException {
//        List<AnnotateImageRequest> requests = new ArrayList<>();
//
//        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));
//
//        Image img = Image.newBuilder().setContent(imgBytes).build();
//        Feature feat = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
//        requests.add(request);
//
//        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
//            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
//            List<AnnotateImageResponse> responses = response.getResponsesList();
//
//            for (AnnotateImageResponse res : responses) {
//                if (res.hasError()) {
//                    out.printf("Error: %s\n", res.getError().getMessage());
//                    return;
//                }
//
//                // For full list of available annotations, see http://g.co/cloud/vision/docs
//                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
//                    annotation.getAllFields().forEach((k, v) -> out.printf("%s : %s\n", k, v.toString()));
//                }
//            }
//        }
//    }
}
