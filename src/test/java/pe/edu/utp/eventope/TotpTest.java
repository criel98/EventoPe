package pe.edu.utp.eventope;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.api.Test;
import pe.edu.utp.eventope.util.*;
import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
class TotpTest {
    static Stream<Arguments> vectores(){
        long[] tiempos={59L,1111111109L,1111111111L,1234567890L,2000000000L,20000000000L};
        String[][] codigos={{"94287082","46119246","90693936"},{"07081804","68084774","25091201"},{"14050471","67062674","99943326"},{"89005924","91819424","93441116"},{"69279037","90698825","38618901"},{"65353130","77737706","47863826"}};
        String[] algoritmos={"HmacSHA1","HmacSHA256","HmacSHA512"};int[] largos={20,32,64};
        var lista=new java.util.ArrayList<Arguments>();
        for(int i=0;i<tiempos.length;i++)for(int j=0;j<3;j++)lista.add(Arguments.of(tiempos[i],algoritmos[j],largos[j],codigos[i][j]));return lista.stream();
    }
    @ParameterizedTest @MethodSource("vectores") void rfc6238(long tiempo,String algoritmo,int largo,String esperado){
        byte[] clave="1234567890".repeat(7).substring(0,largo).getBytes(StandardCharsets.US_ASCII);
        assertEquals(esperado,Totp.generar(clave,Instant.ofEpochSecond(tiempo),algoritmo));
    }
    @Test void payloadVenceEnLimite(){byte[] s=Totp.nuevaClave();var a=Instant.ofEpochSecond(59);var p=new QrPayload("00000000-0000-4000-8000-000000000001",Totp.intervalo(a),Totp.generar(s,a));assertTrue(QrPayload.leer(p.codificar()).verificar(s,a));assertFalse(p.verificar(s,Instant.ofEpochSecond(60)));assertEquals(60,Totp.expiracion(a).getEpochSecond());}
    @Test void rechazaMalformed(){assertThrows(IllegalArgumentException.class,()->QrPayload.leer("hola"));assertThrows(IllegalArgumentException.class,()->QrPayload.leer("EP1:1:1:12345678"));assertThrows(IllegalArgumentException.class,()->Totp.generar(new byte[2],Instant.now()));}
}
