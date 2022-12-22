package co.dalicious.domain.food.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@DynamicInsert
@DynamicUpdate
@Getter
@NoArgsConstructor
@Entity
@Table(name = "food__food")
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Integer price;

    @Column(name = "img")
    private String img;

    @Column(name = "makers__makers_id")
    private String makersId;

    @Column(name = "description")
    private String description;

    @Builder
    Food(Integer id, String name, Integer price, String img, String makersId, String description){
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
        this.makersId = makersId;
        this.description = description;
    }

}
