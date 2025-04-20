package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface CashCardRepository extends
        CrudRepository<CashCard, Long>,
        PagingAndSortingRepository<CashCard, Long> {
    CashCard findByIdAndOwner(Long id, String owner); // Custom method to find by ID and owner

    Page<CashCard> findByOwner(String owner, PageRequest pageRequest); // Custom method to find by owner with pagination
}
