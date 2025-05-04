import org.springframework.data.jpa.repository.JpaRepository;

public interface AddrRepository extends JpaRepository<Addr, Addr.AddrKey> {
}
