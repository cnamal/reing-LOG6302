public class Fibo{

    public int fib(int n){
        int i = n-1;
        int a = 1;
        int b = 0;
        int c = 0;
        int d = 1;
        int t;
        if(n<=0)
            return 0;
        while(i>0){
            while(i%2==0){
                t = d*(2*c+d);
                c = c*c +d*d;
                d = t;
                i = i/2;
            }
            t = d*(b+a) + c*b;
            a = d*b+c*a;
            b = t;
            i=i-1;
        }
        return a+b;
    }
}
