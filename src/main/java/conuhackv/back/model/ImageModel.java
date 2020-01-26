package conuhackv.back.model;

public class ImageModel {
    private Integer id;

    private Integer cameraId;

    private byte[] image;

    public ImageModel() {
    }

    public ImageModel(Integer id, Integer cameraId, byte[] image) {
        this.id = id;
        this.cameraId = cameraId;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
