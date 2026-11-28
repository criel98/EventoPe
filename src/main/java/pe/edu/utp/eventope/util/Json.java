package pe.edu.utp.eventope.util;

import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

/** Serializador JSON mínimo para las respuestas de la API; no añade dependencias al POM. */
public final class Json {
    private Json() {}
    public static String serializar(Object valor) {
        if(valor==null)return "null";
        if(valor instanceof String||valor instanceof Character||valor instanceof TemporalAccessor)return comillas(valor.toString());
        if(valor instanceof BigDecimal decimal)return decimal.toPlainString();
        if(valor instanceof Number||valor instanceof Boolean)return valor.toString();
        if(valor instanceof Map<?,?> mapa){
            StringBuilder b=new StringBuilder("{");boolean primero=true;
            for(Map.Entry<?,?> e:mapa.entrySet()){if(!primero)b.append(',');primero=false;b.append(comillas(String.valueOf(e.getKey()))).append(':').append(serializar(e.getValue()));}
            return b.append('}').toString();
        }
        if(valor instanceof Iterable<?> lista){StringBuilder b=new StringBuilder("[");boolean primero=true;for(Object elemento:lista){if(!primero)b.append(',');primero=false;b.append(serializar(elemento));}return b.append(']').toString();}
        throw new IllegalArgumentException("Tipo no serializable como JSON: "+valor.getClass().getName());
    }
    private static String comillas(String texto){
        StringBuilder b=new StringBuilder("\"");
        for(int i=0;i<texto.length();i++){char ch=texto.charAt(i);switch(ch){
            case '"'->b.append("\\\"");case '\\'->b.append("\\\\");case '\b'->b.append("\\b");case '\f'->b.append("\\f");case '\n'->b.append("\\n");case '\r'->b.append("\\r");case '\t'->b.append("\\t");
            default->{if(ch<0x20)b.append(String.format("\\u%04x",(int)ch));else b.append(ch);}
        }}
        return b.append('"').toString();
    }
}
