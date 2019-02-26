package http.util.future;

/**
 *
 *
 * @author winflex
 */
public interface IProgressiveFutureListener extends IFutureListener {
    
    void operationProgressed(IProgressiveFuture future, long progress, long total) throws Exception;
}
