package http.util.future;


/**
 * 
 *
 * @author winflex
 */
public interface IProgressivePromise extends IPromise, IProgressiveFuture {
    
    IProgressivePromise setProgress(long progress, long total);
}
