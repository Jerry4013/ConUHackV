package conuhackv.back.dataobject;

import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

/**
 * @author: Jingchao Zhang
 * @createDate: 2019/11/05
 **/
@Entity
@DynamicInsert
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column()
    private Integer cameraId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

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
