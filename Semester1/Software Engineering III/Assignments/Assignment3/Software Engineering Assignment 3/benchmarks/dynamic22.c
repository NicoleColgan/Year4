# include <stdlib.h>
# include <stdio.h>
# include <math.h>
# include <time.h>
# include <omp.h>

//paralellise program
//optimise program
//benchmark program

//using OpenMp directives make a paralell version of this 
//show where "fine-grain" parralel execution using "schedule( directive brings performance benefits
//justify decisions
#define MSIZE	1000

int main(void)

{
    // double a[MSIZE][MSIZE], b[MSIZE][MSIZE], c[MSIZE][MSIZE];
    double* a, * b, * c;
    int i, j, k;
    int n = MSIZE;
    double pi = 3.141592653589793;
    double s, angle;
    double wtime;


    printf("\n");
    printf("  Compute matrix product C = A * B\n");

    printf("\n");
    printf("  The number of processors available = %d\n", omp_get_num_procs());
    printf("  The number of threads available    = %d\n", omp_get_max_threads());

    printf("  The matrix order N                 = %d\n", n);

    //allocate enough memory for each matrix
    a = malloc(MSIZE * MSIZE * sizeof(double));
    b = malloc(MSIZE * MSIZE * sizeof(double));
    c = malloc(MSIZE * MSIZE * sizeof(double));

    /*
    Loop 1: Evaluate A.
    */
    s = 1.0 / sqrt((double)(n));

    wtime = omp_get_wtime();

    //scheduling one for loop
    #pragma omp parallel for schedule(dynamic,2) num_threads(2)
    for (i = 0; i < n; i++) //1000 iterations
    {
        #pragma omp parallel for schedule(dynamic,2) num_threads(2)
        for (j = 0; j < n; j++) //1000x1000 iterations
        {
            angle = 2.0 * pi * i * j / (double)n;
            // a[i][j] = s * (sin (angle) + cos (angle));
            a[i + MSIZE * j] = s * (sin(angle) + cos(angle));
        }
    }

    /*
      Loop 2: Copy A into B.
    */
    //#pragma omp parallel for schedule(static,8)
    for (i = 0; i < n; i++) ///1000 iterations
    {
        //#pragma omp parallel for schedule(static,8)
        for (j = 0; j < n; j++) //1000 iteration
        {
            b[i + MSIZE * j] = a[i + MSIZE * j];
        }
    }

    /*
      Loop 3: Compute C = A * B.
    */
    //#pragma omp parallel for schedule(static,8)
    for (i = 0; i < n; i++) //1000 iterationa=s
    {
        #pragma omp parallel for schedule(dynamic,2) num_threads(2)
        for (j = 0; j < i; j++) // n
        {
            c[i + MSIZE * j] = 0.0;
            //#pragma omp parallel for schedule(static,8)
            for (k = 0; k < j; k++) // n
            {
                c[i + MSIZE * j] += sin(a[i + MSIZE * k] * b[k + MSIZE * j]);
            }
        }
    }

    wtime = omp_get_wtime() - wtime;

    printf("  Elapsed time = %g seconds\n", wtime);

    printf("\n");
    printf("  Normal end of execution.\n");

    free(a);
    free(b);
    free(c);

    return 0;
}
/******************************************************************************/
