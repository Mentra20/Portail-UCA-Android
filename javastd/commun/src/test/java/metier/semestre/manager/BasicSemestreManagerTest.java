package metier.semestre.manager;

import metier.semestre.rules.SemestreRules;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.Spy;

public class BasicSemestreManagerTest {

    @Spy
    BasicSemestreManager manager;

    @Mock
    SemestreRules rules;

    @BeforeEach
    public void init(){

    }
}