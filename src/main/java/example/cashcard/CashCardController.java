package example.cashcard;

import java.security.Principal;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/cashcards")
class CashCardController {

    private final CashCardRepository cashCardRepository;

    CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestId, Principal principal) {
        var fetchedCashCard = findCashCardByIdAndOwner(requestId, principal.getName());

        if (fetchedCashCard.isPresent()) {
            return ResponseEntity.ok(fetchedCashCard.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(
            @RequestBody CashCard newCashCardRequest,
            UriComponentsBuilder ucb,
            Principal principal) {
        var newCashCardWithOwner = new CashCard(
                null,
                newCashCardRequest.amount(),
                principal.getName());

        var savedCashCard = cashCardRepository.save(newCashCardWithOwner);
        var locationOfNewCashCard = ucb
                .path("/cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity
                .created(locationOfNewCashCard)
                .build();
    }

    @PutMapping("/{requestId}")
    private ResponseEntity<Void> updateCashCard(
            @PathVariable Long requestId,
            @RequestBody CashCard updatedCashCardRequest,
            Principal principal) {
        var fetchedCashCard = findCashCardByIdAndOwner(requestId, principal.getName());

        if (fetchedCashCard.isPresent()) {
            var updatedCashCard = new CashCard(
                    fetchedCashCard.get().id(),
                    updatedCashCardRequest.amount(),
                    principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping
    private ResponseEntity<Iterable<CashCard>> findAll(Pageable pageable, Principal principal) {
        var pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSortOr(Sort.by(Direction.ASC, "amount")));

        var PageOfcashCards = cashCardRepository.findByOwner(principal.getName(), pageRequest);
        return ResponseEntity.ok(PageOfcashCards.getContent());
    }

    @DeleteMapping("/{requestId}")
    private ResponseEntity<Void> deleteCashCard(
            @PathVariable Long requestId,
            Principal principal) {
        var fetchedCashCard = findCashCardByIdAndOwner(requestId, principal.getName());

        if (fetchedCashCard.isPresent()) {
            cashCardRepository.delete(fetchedCashCard.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    private Optional<CashCard> findCashCardByIdAndOwner(Long requestId, String name) {
        return Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestId, name));
    }

}
