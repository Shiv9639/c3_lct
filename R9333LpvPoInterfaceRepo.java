package com.C3Collection.C3.Repository;

import com.C3Collection.C3.Model.C3Master;
import com.C3Collection.C3.Model.R9333LpvPoInterface;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface R9333LpvPoInterfaceRepo extends MongoRepository<R9333LpvPoInterface, String> {
    R9333LpvPoInterface findByPurchaseOrderId(String po_number);
}
