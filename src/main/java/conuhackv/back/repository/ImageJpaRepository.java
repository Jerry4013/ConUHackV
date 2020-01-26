package conuhackv.back.repository;

import conuhackv.back.dataobject.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author: Jingchao Zhang
 * @createdate: 2019/07/04
 **/
@Repository
public interface ImageJpaRepository extends JpaRepository<Image, Integer> {

}
