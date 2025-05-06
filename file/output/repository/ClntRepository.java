import org.springframework.data.jpa.repository.JpaRepository;

public interface ClntRepository extends JpaRepository<Clnt, String>, ClntCustomRepository{
}
