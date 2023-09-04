package wizmokeycloak.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import wizmokeycloak.SalesApplication;
import wizmokeycloak.domain.SalesOrderCreated;
import wizmokeycloak.domain.SalesOrderDeleted;
import wizmokeycloak.domain.SalesOrderUpdated;

@Entity
@Table(name = "SalesOrder_table")
@Data
//<<< DDD / Aggregate Root
public class SalesOrder {

    @Id
    private String salesOrderNumber;

    private String salesPerson;

    @Embedded
    private CompanyId companyId;

    private String status;

    @ElementCollection
    private List<SalesItems> salesItems;

    private SalesType salesType;

    @PostPersist
    public void onPostPersist() {
        SalesOrderCreated salesOrderCreated = new SalesOrderCreated(this);
        salesOrderCreated.publishAfterCommit();

        SalesOrderUpdated salesOrderUpdated = new SalesOrderUpdated(this);
        salesOrderUpdated.publishAfterCommit();

        SalesOrderDeleted salesOrderDeleted = new SalesOrderDeleted(this);
        salesOrderDeleted.publishAfterCommit();
    }

    @PreRemove
    public void onPreRemove() {}

    public static SalesOrderRepository repository() {
        SalesOrderRepository salesOrderRepository = SalesApplication.applicationContext.getBean(
            SalesOrderRepository.class
        );
        return salesOrderRepository;
    }

    //<<< Clean Arch / Port Method
    public void produce(ProduceCommand produceCommand) {
        //implement business logic here:

        Produced produced = new Produced(this);
        produced.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
