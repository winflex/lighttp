package http.util.future;

/**
 * A writeable {@link IFuture}
 * 
 * @author winflex
 */
public interface IPromise extends IFuture {
    
    IPromise setSuccess(Object result);
    
    IPromise setFailure(Throwable cause);
}
