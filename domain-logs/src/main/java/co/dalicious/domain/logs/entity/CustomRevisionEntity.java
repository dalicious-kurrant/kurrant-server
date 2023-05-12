package co.dalicious.domain.logs.entity;

import co.dalicious.domain.logs.entity.listener.CustomRevisionListener;
import lombok.Getter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;

import javax.persistence.*;

@Entity
@Getter
@RevisionEntity(CustomRevisionListener.class)
public class CustomRevisionEntity extends DefaultRevisionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "id", nullable = false)
    private Long id;

    private String username;
    private String changes;

    public void setId(Long id) {
        this.id = id;
    }

    public void addChange(String change) {
        if (changes == null) {
            changes = change;
        } else {
            changes = changes + ", " + change;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setChanges(String changes) {
        this.changes = changes;
    }
}
