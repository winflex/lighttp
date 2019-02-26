package http.util.future;

/**
 * 
 *
 * @author winflex
 */
public interface IFutureListener {

    void operationCompleted(IFuture future) throws Exception;
}
